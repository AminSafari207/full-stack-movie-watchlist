module.exports = {
  customChunkNames: [
    {
      resourcePath: 'src/Data/ApiErrors/uniqueCodeTypes.json',
      chunkName: 'ufonii-unique-code-types',
    },
    {
      resourcePath: 'src/Data/ApiErrors/ufoniiExceptions.json',
      chunkName: 'ufonii-api-exceptions',
    },
    {
      resourcePath: 'src/Components/Features/UQuotes/SubComponents/Quotes/StyledComps.js',
      chunkName: 'ufonii-quotes-crud',
    },
    {
      resourcePath: '@mui/x-data-grid',
      chunkName: 'mui-x-data-grid',
    },
  ],
}
