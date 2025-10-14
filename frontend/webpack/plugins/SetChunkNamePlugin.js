const { customChunkNames } = require('../data/chunkNames')

class SetChunkNamePlugin {
  constructor(options = {}) {
    this.options = options
  }

  apply(compiler) {
    compiler.hooks.compilation.tap('SetChunkNamePlugin', (compilation) => {
      compilation.hooks.afterOptimizeChunks.tap('SetChunkNamePlugin', (chunks) => {
        const chunkGraph = compilation.chunkGraph

        chunks.forEach((chunk) => {
          const chunkModules = Array.from(chunkGraph.getChunkModulesIterable(chunk))
          chunkModules.forEach((module) => {
            if (module.resource && this.options.match.test(module.resource)) {
              const chunkName = this.getChunkName(module.resource)
              if (chunkName) {
                chunk.name = chunkName
              }
            }
          })
        })
      })
    })
  }

  getChunkName(resourcePath) {
    const chunk = customChunkNames.find((chunk) => resourcePath.includes(chunk.resourcePath))
    return chunk ? chunk.chunkName : 'defaultChunkName' // Fallback chunk name
  }
}

const generateEscapedPathMatchRegExp = (chunkNames) => {
  const escapedPaths = chunkNames.map((item) => item.resourcePath.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&'))
  return new RegExp(`(${escapedPaths.join('|')})`)
}

module.exports = { SetChunkNamePlugin, generateEscapedPathMatchRegExp }
