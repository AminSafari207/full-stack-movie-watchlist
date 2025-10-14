import { mainApiInstance } from 'src/shared/axios'
import { endPoints } from 'src/shared/constants'

const _get = async (url, ...args) => mainApiInstance.get(url, ...args)
const _post = async (url, ...args) => mainApiInstance.post(url, ...args)
const _delete = async (url, ...args) => mainApiInstance.delete(url, ...args)

// users
export const getUser = (...args) => _get(endPoints.servlet.users, ...args)
export const getUsers = (...args) => _get(endPoints.servlet.users, ...args)
export const addUser = (...args) => _post(endPoints.servlet.users, ...args)
export const deleteUser = (...args) => _delete(endPoints.servlet.users, ...args)
// movies
export const getMovie = (...args) => _get(endPoints.servlet.movies, ...args)
export const getMovies = (...args) => _get(endPoints.servlet.movies, ...args)
export const addMovie = (...args) => _post(endPoints.servlet.movies, ...args)
export const deleteMovie = (...args) => _delete(endPoints.servlet.movies, ...args)
// watchlist
export const getWatchlist = (...args) => _get(endPoints.servlet.watchlist, ...args)
export const getWatchlists = (...args) => _get(endPoints.servlet.watchlist, ...args)
export const addWatchlist = (...args) => _post(endPoints.servlet.watchlist, ...args)
export const deleteWatchlist = (...args) => _delete(endPoints.servlet.watchlist, ...args)
