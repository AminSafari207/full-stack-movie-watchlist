module.exports = {
  singleQuote: true,
  printWidth: 120,
  semi: false,
  endOfLine: 'auto',
  overrides: [
    {
      files: '**/*.json',
      options: {
        trailingComma: 'none',
      },
    },
  ],
  plugins: ['prettier-plugin-organize-imports'],
}
