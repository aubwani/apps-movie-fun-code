package org.superbiz.moviefun;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.albums.AlbumFixtures;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesBean;

import javax.transaction.TransactionManager;
import java.util.Map;

@Controller
public class HomeController {

    private final MoviesBean moviesBean;
    private final AlbumsBean albumsBean;
    private final MovieFixtures movieFixtures;
    private final AlbumFixtures albumFixtures;
    @Autowired
    private PlatformTransactionManager platformTransactionManagerForMovies;
    @Autowired
    private PlatformTransactionManager platformTransactionManagerForAlbums;

    public HomeController(MoviesBean moviesBean, AlbumsBean albumsBean, MovieFixtures movieFixtures, AlbumFixtures albumFixtures) {
        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;

    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {
        TransactionStatus moviesStatus =  platformTransactionManagerForMovies.getTransaction(new DefaultTransactionDefinition());
        try {
            for (Movie movie : movieFixtures.load()) {
                moviesBean.addMovie(movie);
            }
            platformTransactionManagerForMovies.commit(moviesStatus);
        }catch (Exception e){
            platformTransactionManagerForMovies.rollback(moviesStatus);
        }
        TransactionStatus albumsStatus =  platformTransactionManagerForAlbums.getTransaction(new DefaultTransactionDefinition());
        try {
            for (Album album : albumFixtures.load()) {
                albumsBean.addAlbum(album);
            }
            platformTransactionManagerForAlbums.commit(albumsStatus);
        }catch (Exception e){
            platformTransactionManagerForAlbums.rollback(albumsStatus);
        }

        model.put("movies", moviesBean.getMovies());
        model.put("albums", albumsBean.getAlbums());

        return "setup";
    }
}
