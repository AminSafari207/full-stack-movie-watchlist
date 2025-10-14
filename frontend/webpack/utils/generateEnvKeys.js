const generateEnvKeys = (env) => {
  return Object.keys(env).reduce((acc, key) => {
    acc[`process.env.${key}`] = JSON.stringify(env[key])
    return acc
  }, {})
}

module.exports = { generateEnvKeys }
