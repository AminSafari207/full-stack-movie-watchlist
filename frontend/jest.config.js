module.exports = {
  testEnvironment: 'jsdom',
  transform: {
    '^.+\\.[jt]sx?$': 'babel-jest',
  },
  setupFilesAfterEnv: ['<rootDir>/src/setupTests.js'],
  moduleDirectories: ['node_modules', 'src'],
  moduleNameMapper: {
    '\\.(css|less|scss)$': 'identity-obj-proxy',
    '^axios$': 'axios/dist/node/axios.cjs',
  },
  testPathIgnorePatterns: ['/node_modules/'],
  globals: {
    'ts-jest': {
      tsconfig: 'tsconfig.json',
    },
  },
}
