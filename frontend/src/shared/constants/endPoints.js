export const endPoints = {
    fsmw: {
        auth: {
            signupUser: "auth/register",
            loginUser: "auth/login"
        },
        user: {
            getUserProfile: "user/getprofile",
            editUserProfile: "user/editprofile"
        },
        movie: {
            getMovies: "movie/getmovies",
            addMovie: "movie/addmovie",
            editMovie: "movie/editmovie",
            deleteMovie: "movie/deletemovie"
        },
        watchlist: {
            getWatchlist: "watchlist/getwatchlist",
            addWatchlist: "watchlist/addwatchlist",
            deleteWatchlist: "watchlist/deletewatchlist"
        }
  }
}
