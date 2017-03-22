package unileao.edu.br.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch;

    private Texture[] passaros;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;

    private float larguraDispositivo = 0;
    private float alturaDispositivo = 0;
    private int estadoJogo = 0;

    private float variacao = 0;
    private float velocidadeQueda = 0;
    private float posicaoInicialVertical = 0;
    private float posicaoMovimentoCanoHorizontal;
    private float espacoEntreCanos;
    private float deltaTime;
    private Random numeroRandomico;
    private float alturaentrecanosrandomica;
    private BitmapFont fonte;
    private int pontuacao = 0;
    private boolean marcouponto= false;
    private Circle passarocirculo;
    private Rectangle canotopo;
    private Rectangle canobaixo;
    private ShapeRenderer shape;
    private Texture gameover;
    private BitmapFont mensagem;
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float virtualWidth = 768;
    private final float virtualHeight = 1024;

	@Override
	public void create () {
        batch = new SpriteBatch();
        numeroRandomico = new Random();
        passarocirculo = new Circle();
        shape = new ShapeRenderer();

        fonte = new BitmapFont();
        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(6);


        mensagem = new BitmapFont();
        mensagem.setColor(Color.WHITE);
        mensagem.getData().setScale(3);

        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");

        fundo = new Texture("fundo.png");
        canoBaixo = new Texture("cano_baixo.png");
        canoTopo = new Texture("cano_topo.png");
        gameover = new Texture("game_over.png");

        camera = new OrthographicCamera();
        camera.position.set(virtualWidth/2,virtualHeight/2,0);
        viewport = new StretchViewport(virtualWidth, virtualHeight, camera);


        larguraDispositivo = virtualWidth;
        alturaDispositivo = virtualHeight;

        posicaoInicialVertical = Gdx.graphics.getHeight()/2;
        posicaoMovimentoCanoHorizontal = larguraDispositivo;
        espacoEntreCanos = 300;
	}

	@Override
	public void render () {

        camera.update();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        deltaTime = Gdx.graphics.getDeltaTime();
        variacao += deltaTime * 5;
        if (variacao > 2) variacao = 0;

        if(estadoJogo == 0){
            if(Gdx.input.justTouched()){
                estadoJogo =1;
            }
        }
        else {
            velocidadeQueda += deltaTime *50;
            if (posicaoInicialVertical > 0 || velocidadeQueda < 0)
                posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;
                if(estadoJogo == 1){
                    posicaoMovimentoCanoHorizontal -= deltaTime * 150;

                    if (Gdx.input.justTouched()) {
                        velocidadeQueda = -15;
                    }
                    if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
                        posicaoMovimentoCanoHorizontal = larguraDispositivo;
                        alturaentrecanosrandomica = numeroRandomico.nextInt(400) - 200;
                        marcouponto = false;
                    }

                    if(posicaoMovimentoCanoHorizontal < 120){
                        if(!marcouponto) {
                            pontuacao++;
                            marcouponto = true;
                        }
                    }

                }
                else {
                    if(Gdx.input.justTouched()){
                        estadoJogo = 0;
                        pontuacao = 0;
                        velocidadeQueda = 0;
                        posicaoInicialVertical = alturaDispositivo/2;
                        posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    }
                }
        }

        batch.setProjectionMatrix( camera.combined);

        batch.begin();

        batch.draw(fundo,0,0, larguraDispositivo, alturaDispositivo);
        batch.draw(canoTopo,posicaoMovimentoCanoHorizontal,alturaDispositivo/2 + espacoEntreCanos/2 + alturaentrecanosrandomica);
        batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal,alturaDispositivo/2 - canoBaixo.getHeight() - espacoEntreCanos/2 + alturaentrecanosrandomica);
        batch.draw(passaros[ (int) variacao] , 120, posicaoInicialVertical);
        fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo/2, alturaDispositivo - 50);

        if(estadoJogo == 2){
            batch.draw(gameover, larguraDispositivo/2 - gameover.getWidth()/2, alturaDispositivo/2);
            mensagem.draw(batch,"Toque Para Reiniciar!", larguraDispositivo/2 - gameover.getWidth()/2, alturaDispositivo/2 - gameover.getHeight()/2);

        }

        batch.end();

        passarocirculo.set(120 + passaros[0].getWidth()/2, posicaoInicialVertical + passaros[0].getHeight()/2, passaros[0].getWidth()/2);
        canobaixo = new Rectangle(
                posicaoMovimentoCanoHorizontal,alturaDispositivo/2 - canoBaixo.getHeight() - espacoEntreCanos/2 + alturaentrecanosrandomica
                ,canoBaixo.getWidth(), canoBaixo.getHeight()
        );
        canotopo = new Rectangle(
                posicaoMovimentoCanoHorizontal,alturaDispositivo/2 + espacoEntreCanos/2 + alturaentrecanosrandomica,
                canoTopo.getWidth(), canoTopo.getHeight()
        );

        /*shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.circle(passarocirculo.x, passarocirculo.y, passarocirculo.radius);
        shape.setColor(Color.RED);
        shape.rect(canobaixo.x, canobaixo.y, canobaixo.width, canobaixo.height);
        shape.rect(canotopo.x, canotopo.y, canotopo.width, canotopo.height);
        shape.end();*/

        if(Intersector.overlaps(passarocirculo, canobaixo) || Intersector.overlaps(passarocirculo, canotopo)
                || posicaoInicialVertical<=0 || posicaoInicialVertical >= alturaDispositivo){

            estadoJogo =2;

        }
	}

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
    }
}
