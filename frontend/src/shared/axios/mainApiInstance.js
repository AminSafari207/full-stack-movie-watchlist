import axios from 'axios'

let cancelTokenSource = axios.CancelToken.source() || null

export const mainApiInstance = axios.create({
  baseURL: `${process.env.SERVLET_PROTOCOL}://${process.env.SERVLET_BASE_URL}:${process.env.SERVLET_PORT}/fsmw/`,
  headers: { 'Content-Type': 'application/json' },
})

export const cancelMainApi = (message = 'Request canceled') => {
  if (cancelTokenSource) {
    cancelTokenSource.cancel(message)
  }
  cancelTokenSource = axios.CancelToken.source()
}
