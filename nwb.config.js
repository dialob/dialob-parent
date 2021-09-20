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
  }
}
