// const envToNumber = (value) => {
//   const returnNumber = Number(value)

//   if (Number.isNaN(returnNumber)) return undefined
//   return returnNumber
// }

const envToBoolean = (value) => {
  if (value === 'true') return true
  if (value === 'false') return false
  return undefined
}

const isProd = process.env.NODE_ENV === 'production'
const isLive = envToBoolean(process.env.HOT)
const appUrl = isLive ? process.env.WEBPACK_SERVER : process.env.REACT_APP_URL

export const env = {
  isProd,
  appUrl,
}
