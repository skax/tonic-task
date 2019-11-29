package kmiecik.michal.tonictask.infrastructure.rest

import kmiecik.michal.tonictask.films.FilmsFacade
import kmiecik.michal.tonictask.films.api.NewCatalogDto
import kmiecik.michal.tonictask.infrastructure.rest.helpers.ServerResponseCreator
import kmiecik.michal.tonictask.infrastructure.security.SecurityWrapper
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

class FilmHandler(private val filmsFacade: FilmsFacade, private val securityWrapper: SecurityWrapper, private val serverResponseCreator: ServerResponseCreator) {

    fun routes() = router {
        "/films".nest {
            POST("/catalog", this@FilmHandler::createFilmCatalog)
            GET("/list", this@FilmHandler::listFilm)
        }
    }

    private fun createFilmCatalog(req: ServerRequest): Mono<ServerResponse> {
        return securityWrapper.onlyOwners(req) { request, _ ->
            request.bodyToMono<NewCatalogDto>().flatMap {
                filmsFacade.createFilmCatalog(it)
                        .let { mono -> serverResponseCreator.okFromMono { mono } }
            }
        }
    }

    private fun listFilm(req: ServerRequest): Mono<ServerResponse> {
        return filmsFacade.listFilms()
                .let { serverResponseCreator.okFromMono { it } }
    }
}
