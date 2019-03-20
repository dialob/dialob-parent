module.exports = {
  type: 'react-component',
  npm: {
    esModules: true,
    umd: {
      global: 'DialobComposer',
      externals: {
        react: 'React'
      }
    }
  },

  webpack: {
    html: {
      template: 'demo/src/index.html'
    },
    extractCSS: {
      filename: '[name].css'
    },
    extra: {
      output: {
        filename: '[name].js',
        chunkFilename: '[name].js'
      }
    }
  },
  karma: {
    testFiles: ['tests/**/*Test.js'],
    frameworks: ['mocha', 'chai', 'chai-immutable'],
    plugins: [
      require('karma-chai-plugins'),
      require('karma-chai-immutable')
    ]
  }
}
