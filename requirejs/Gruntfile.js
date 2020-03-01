module.exports = function(grunt) {
  const srcPath = "src";
  const destPath = "dist";

  // Project configuration.
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    requirejs: {
      compile: {
        /*
          - 빌드 환경이 개발 환경과 다를 경우 아래에 설정을 재정의 할 수 있다.
        */
        options: {
          mainConfigFile: srcPath + "/js/config.js",
          /*
          */
          baseUrl: srcPath + '/js',
          optimize: "none",
          /*
            - 모듈들을 종속성에 맞춰 Tree 형태의 Code로 생성한다.
            - 일반적으로는 data-main애 명시된 js를 명시한다.
          */
          name: "../../node_modules/almond/almond",
          /*
            - almond 사용시 data-main의 js를 명시
          */
          include: ["main.js"],
          findNestedDependencies: true,
          skipModuleInsertion: true,
          wrapShim: true,
          out: destPath + '/js/main.js',
          wrap: true,
          /*
            - amdclean을 실행하여 amd 모듈들의 종송석을 non-amd 로 변환한다.
          */
          onModuleBundleComplete: function (data) {
            var fs = require('fs'),
              amdclean = require('amdclean'),
              outputFile = data.path;

            fs.writeFileSync(outputFile, amdclean.clean({
              'filePath': outputFile
            }));
          }
        }
      }
    },
    uglify: {
      options: {
        banner: '/*! v<%= pkg.version %> */\n',
        // beautify: true,
        /*
          - 일반적으로 'properties: true' 설정할 수 있으나, 라이브러리의 function도 uglify되어 정상 동작하지 않는다.
          - uglify 대상의 properties를 특정 패턴으로 작성하고, regex에 지정한다.
        */
        mangle: {
          properties: {
            regex: /^V_/
          },
          toplevel: true
        }
      },
      build: {
        src: destPath + "/js/main.js",
        dest: destPath + "/js/main.js"
      }
    },
    copy: {
      index: {
        src: srcPath + "/index.html",
        dest: destPath + "/index.html",
        options: {
          process: function(content, srcpath) {
            /*
              - 개발 시에는 requireJS를 사용하지만, 빌드 후 배포시에는 single js를 사용하기 위해 script 정의를 변경한다.
            */
            return content
              .replace(/<script.*src=\"js\/config\.js\".*<\/script>\n?/m, "") // remove require config js
              .replace(/<script.*data-main=\"js\/main\".*<\/script>\n?/m, "") // remove requirejs
              .replace(/<\/head>/, "<script type=\"text/javascript\" src=\"js/main.js\"></script>\n$&") // add minified js
              ;
          }
        }
      }
    }
  });

  grunt.loadNpmTasks('grunt-contrib-requirejs');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-copy');

  // Default task(s).
  grunt.registerTask('default', ['requirejs', 'uglify', 'copy:index']);

};