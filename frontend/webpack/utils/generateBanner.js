const fs = require('fs')
const lodash = require('lodash')

const generateBanner = (bannerPath, bannerData) => {
  const template = fs.readFileSync(bannerPath, 'utf8')
  const compiled = lodash.template(template)

  return compiled(bannerData)
}

module.exports = { generateBanner }
