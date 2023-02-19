[![codecov](https://codecov.io/gh/seamuslowry/brannigans-chess-backend/branch/main/graph/badge.svg?token=4VLU6PJ0ZL)](https://codecov.io/gh/seamuslowry/brannigans-chess-backend)

# Brannigan's Chess

"In the game of chess, you can never let your adversary see your pieces." -Zapp Brannigan

## Running the Project

- This project is best run from the [core repo](https://github.com/seamuslowry/brannigans-chess)
- Build the container with `docker-compose build backend`
- Run `docker-compose up backend` to bring up the service
- You should now see the service exposed on `localhost:8080`. Swagger for testing can be used on `localhost:8080/swagger-ui.html`

## Goal

This is designed as a chess app in which each player can only see their own pieces. Taken pieces are visible to both players.

## End of Development

The project is no longer in active development, and this portion is no longer deployed to save on costs. It can still be run locally from the [core repo](https://github.com/seamuslowry/brannigans-chess).
