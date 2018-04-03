module.exports = {
  type: 'react-app',

  webpack: {
    extra: {
      output: {
        filename: '[name].js',
        chunkFilename: '[name].js'
      }
    }
  }
}
