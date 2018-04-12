module.exports = {
  type: 'react-app',

  webpack: {
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
