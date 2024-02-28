package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameScreen implements Screen {
    final Bird game;
    OrthographicCamera camera;
    Stage stage;
    Player player;
    boolean dead;
    Array<Pipe> obstacles;
    List<Coin> coins;

    long lastObstacleTime;
    float score;
    float baseSpeedX = -200;
    boolean speedAument = true;
    int nivel;

    public GameScreen(final Bird gam) {
        this.game = gam;
        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        player = new Player();
        player.setManager(game.manager);
        stage = new Stage();
        stage.getViewport().setCamera(camera);
        stage.addActor(player);
        // create the obstacles array and spawn the first obstacle
        obstacles = new Array<Pipe>();
        coins = new ArrayList<Coin>();

        spawnObstacle();
        coinllamar();
        score = 0;
        nivel=1;
    }
    @Override
    public void render(float delta) {
        boolean dead = false;
        // clear the screen with a color
        ScreenUtils.clear(0.3f, 0.8f, 0.8f, 1);
        // tell the camera to update its matrices.
        camera.update();
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);
        // begin a new batch
        game.batch.begin();
        game.batch.draw(game.manager.get("background.png", Texture.class), 0,
                0);
        game.batch.end();
        // Stage batch: Actors
        stage.getBatch().setProjectionMatrix(camera.combined);
        stage.draw();


        // process user input
        if (Gdx.input.justTouched()) {
            game.manager.get("flap.wav", Sound.class).play();
            player.impulso();
        }
        // Comprova que el jugador no es surt de la pantalla.
        // Si surt per la part inferior, game over
        if (player.getBounds().y > 480 - 45)
            player.setY( 480 - 45 );
        if (player.getBounds().y < 0 - 45) {
            dead = true;
        }
        if ((int) score % 5 == 0) {
            if (speedAument) {
                baseSpeedX *= 1.01f;

                // Ajustar la velocidad de los nuevos obstáculos si su velocidad no ha sido ajustada aún
                for (Pipe obstacle : obstacles) {
                    obstacle.setSpeedX(obstacle.getSpeedX() * 1.01f);
                }

                speedAument = false;
            }
            else speedAument = true;
        }

        // Comprova si cal generar un obstacle nou
        if (TimeUtils.nanoTime() - lastObstacleTime > 1500000000){
            spawnObstacle();
            coinllamar();
        }

        // Comprova si les tuberies colisionen amb el jugador
        Iterator<Pipe> iter = obstacles.iterator();
        game.batch.begin();
        game.smallFont.draw(game.batch, "Score: " + (int)score, 10,
                470);
        game.batch.end();

        Iterator<Coin> iter2 = coins.iterator();
        while (iter2.hasNext()) {
            Coin coin = iter2.next();
            if (coin.getBounds().overlaps(player.getBounds())) {
                iter2.remove(); // Eliminar la moneda de la lista
                score += (int) (Math.random() * 10) + 5;
                coin.remove();
                if (score > 50) {
                    if (speedAument) {
                        baseSpeedX *= 0.9f;
                        speedAument = false;
                        if (baseSpeedX < -200f){
                            baseSpeedX = 225f;
                        }
                    } else speedAument = true;
                }
            }
        }

        //La puntuació augmenta amb el temps de joc
        score += Gdx.graphics.getDeltaTime();
        while (iter.hasNext()) {
            Pipe pipe = iter.next();
            if (pipe.getBounds().overlaps(player.getBounds())) {
                dead = true;
            }
        }
        // Treure de l'array les tuberies que estan fora de pantalla
        iter = obstacles.iterator();
        while (iter.hasNext()) {
            Pipe pipe = iter.next();
            if (pipe.getX() < -64) {
                obstacles.removeValue(pipe, true);
            }
        }
        if(dead)
        {
            game.manager.get("fail.wav", Sound.class).play();
            game.lastScore = (int)score;
            if(game.lastScore > game.topScore) {
                game.topScore = game.lastScore;
            }
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
        stage.act();

    }



    private void spawnObstacle() {
        // Calcula la alçada de l'obstacle aleatòriament
        float holey = MathUtils.random(100, 429);
        // Crea dos obstacles: Una tubería superior i una inferior

        Pipe pipe1 = new Pipe();
        pipe1.setX(800);
        pipe1.setY(holey);
        pipe1.setUpsideDown(true);
        pipe1.setManager(game.manager);
        obstacles.add(pipe1);
        stage.addActor(pipe1);
        lastObstacleTime = TimeUtils.nanoTime();

    }

    private void coinllamar() {
        // Generar moneda en posición aleatoria en el eje Y
        int x = (int) (Math.random() * 1000)+400;
        int y = (int) (Math.random() * 300)+100;
        Pipe pipe1 = new Pipe();
        Coin coin1 = new Coin();
        coin1.setX(x);
        coin1.setY(y);
        coin1.setManager(game.manager);
        coins.add(coin1);
        stage.addActor(coin1);
        lastObstacleTime = TimeUtils.nanoTime();
    }


    @Override
    public void resize(int width, int height) {
    }
    @Override
    public void show() {
    }
    @Override
    public void hide() {
    }
    @Override
    public void pause() {
    }
    @Override
    public void resume() {
    }
    @Override
    public void dispose() {
    }
}