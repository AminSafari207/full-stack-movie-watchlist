import { mainApiInstance } from 'src/shared/axios'
import { endPoints } from 'src/shared/constants'

const _post = async (url, ...args) => mainApiInstance.post(url, ...args)

// auth
export const signupUser = (...args) => _post(endPoints.fsmw.auth.signupUser, ...args)
export const loginUser = (...args) => _post(endPoints.fsmw.auth.loginUser, ...args)

// user
export const getUserProfile = (...args) => _post(endPoints.fsmw.user.getUserProfile, ...args)
export const editUserProfile = (...args) => _post(endPoints.fsmw.user.editUserProfile, ...args)

// movie
export const getMovies = (...args) => _post(endPoints.fsmw.movie.getMovies, ...args)
export const addMovie = (...args) => _post(endPoints.fsmw.movie.addMovie, ...args)
export const editMovie = (...args) => _post(endPoints.fsmw.movie.editMovie, ...args)
export const deleteMovie = (...args) => _post(endPoints.fsmw.movie.deleteMovie, ...args)

// watchlist
export const getWatchlist = (...args) => _post(endPoints.fsmw.watchlist.getWatchlist, ...args)
export const addWatchlist = (...args) => _post(endPoints.fsmw.watchlist.addWatchlist, ...args)
export const deleteWatchlist = (...args) => _post(endPoints.fsmw.watchlist.deleteWatchlist, ...args)
