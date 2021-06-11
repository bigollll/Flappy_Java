package com.mygdx.game.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
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

public class jogo extends ApplicationAdapter {

    //Construção
    private SpriteBatch batch;    //tem metodo interno que assoscia as informações do conteudo que vai ser renderizado na tela.

    private Texture[] passaros;   //guarda as imagens do passaro.
    private Texture fundo;        //guarda a imagem do fundo.
    private Texture canoTopo;     //guarda imagem do cano.
    private Texture canoBaixo;    //guarda imagem do cano.
    private Texture gameOver;     //guarda a imagem do game over.
    private Texture flappyLogo;   //guarda a imagem do logo.
    private Texture ouroCoin;     //guarda a imagem da moeda de ouro.
    private Texture prataCoin;    //guarda a imagem da moeda de prata.


    private int pontos = 0;            //pontos ao passar do cano.
    private int pontuacaoMaxima = 0;   //pontos maximos atingidos.
    private int pontuacaoOuro = 10;    //pontuação da moeda
    private int pontuacaoPrata = 5;    //pontuação da moeda
    private int estadoJogo = 0;        //para depois alterar os estados do jogo.


    BitmapFont textoIniciar;            //texto para iniciar
    BitmapFont textoPontuacao;          //texto da pontuação.
    BitmapFont textoReiniciar;          //texto de reiniciar.
    BitmapFont textoMelhorPontuacao;    //texto de melhor pontuação.

    Preferences preferencias;

    //Movimentação e posição
    private int gravidade = 0;                        //para fazer o passaro cair.

    private float variacao = 0;                       //variação de altura pra animação.
    private float posicaoInicialVerticalPassaro = 0;  //posiçao que o passaro vai iniciar.
    private float posicaoCanoHorizontal = 0;          //posição do cano.
    private float posicaoCanoVertical;                //posição do cano
    private float espaçoEntreCanos;                   //espaço entre os canos.
    private float posicaoHorizontalPassaro;           //posição horizontal do passaro.

    private float posicaoCoinHorizontal = 0;
    private float posicaoCoinVertical = 0;

    private boolean passouCano = false;               //se passou ou nao pelo cano.

    private Random random;                            //para randomizar os espaços dos canos.

    //Tela
    private float larguraDispositivo;  //para colocar a largura do celular.
    private float alturaDispositivo;   //para colocar a altura do celular.

    //Colisão
    private ShapeRenderer shapeRenderer;        //renderiza os colliders.
    private Circle circuloPassaro;              //formato collider do passaro.
    private Rectangle retanguloCanoCima;        //formato collider cano cima.
    private Rectangle retanguloCanoBaixo;       //formato collider cano baixo.
    private Circle circuloCoinOuro;             //formato collider coin
    private Circle circuloCoinPrata;            //formato collider coin

    //Sons
    Sound somVoando;      //variavel som.
    Sound somColisão;     //variavel som.
    Sound somPontuacao;   //variavel som.
    Sound somCoin;      //variavel som.

    //camera
    private OrthographicCamera camera;                   //pega o tipo de camera a ser utilizada.
    private Viewport viewport;                           //pega a ViewPort.
    private final float VIRTUAL_WIDTH = 720;             //largura generica das telas.
    private final float VIRTUAL_HEIGHT = 1280;           //altura generica das telas.


    @Override
    public void create() {


        inicializaTexturas();
        inicializaObjetos();

        //Metodo create: puxa e instancia objs na tela, monta a tela.
    }

    @Override
    public void render() {

        Gdx.gl.glClear((GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT));       //economizando na renderização qnd troca de tela.

        verificarEstadoJogo();
        validarPontos();
        desenharTexturas();
        detectarColisão();

        //Metodo render: imprime a parte de layout  e aplica as informações.
    }




    @Override
    public void dispose() {

        //Metodo dispose: entrega a aplicação, retorna dados.
    }

    private void inicializaTexturas() {

        passaros = new Texture[3];                             //instanciando a imagem na interface.
        passaros[0] = new Texture("Red_01.png"); //pega a imagem 1.
        passaros[1] = new Texture("Red_02.png"); //pega a imagem 2.
        passaros[2] = new Texture("Red_03.png"); //pega a imagem 3.

        fundo = new Texture("fundo.png");                        //instanciando a imagem na interface.
        canoTopo = new Texture("cano_topo_maior.png");           //pega a imagem do cano.
        canoBaixo = new Texture("cano_baixo_maior.png");         //pega a imagem do cano.

        ouroCoin = new Texture("AngryCoin_gold.png");
        prataCoin = new Texture("AngryCoin_silver.png");

        flappyLogo = new Texture("Angryflappy_logo.png");        //pega a imagem logo
        gameOver = new Texture("game_over.png");                 //pega imagem do gameOver

    }

    private void inicializaObjetos() {

        batch = new SpriteBatch();                               //instanciando um obj a ser contruido.
        random = new Random();                                   //para randomizar os canos.

        larguraDispositivo = VIRTUAL_WIDTH;                     //pega a largura generica do dispositivo passado na variavel.
        alturaDispositivo = VIRTUAL_HEIGHT;                     //pega a altura generica do dispositivo passado na variavel.
        posicaoInicialVerticalPassaro = alturaDispositivo / 2;  //posiciona o passaro no meio da tela.
        posicaoCanoHorizontal = larguraDispositivo;             //iguala a posiçao do cano com o tamanho da tela.
        espaçoEntreCanos = 350;                                 //espaçamento entre os canos na tela.

        textoPontuacao = new BitmapFont();                      //falando que o texto é um bitmapFont.
        textoPontuacao.setColor(Color.WHITE);                   //colocando cor no texto.
        textoPontuacao.getData().setScale(10);                  //Tamanho da fonte do texto.

        textoMelhorPontuacao = new BitmapFont();                //falando que o texto é um bitmapFont.
        textoMelhorPontuacao.setColor(Color.RED);               //colocando cor no texto.
        textoMelhorPontuacao.getData().setScale(2);             //Tamanho da fonte do texto.

        textoReiniciar = new BitmapFont();                      //falando que o texto é um bitmapFont.
        textoReiniciar.setColor(Color.GREEN);                   //colocando cor no texto.
        textoReiniciar.getData().setScale(2);                   //Tamanho da fonte do texto.

        textoIniciar = new BitmapFont();                      //falando que o texto é um bitmapFont.
        textoIniciar.setColor(Color.GREEN);                   //colocando cor no texto.
        textoIniciar.getData().setScale(2);                   //Tamanho da fonte do texto.

        shapeRenderer = new ShapeRenderer();                    //inicializa os render dos colliders.
        circuloPassaro = new Circle();                          //imprime o collider circulo do passaro.
        retanguloCanoCima = new Rectangle();                    //imprime o collider retangulo do cano.
        retanguloCanoBaixo = new Rectangle();                   //imprime o collider retangulo do cano.
        circuloCoinOuro = new Circle();                         //imprime o collider circulo do coin ouro.
        circuloCoinPrata = new Circle();                        //imprime o collider circulo do coin prata.

        somColisão = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));     //Imprime o som.
        somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));         //Imprime o som.
        somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));   //imprime o som.
        somCoin = Gdx.audio.newSound(Gdx.files.internal("som-de-moedas.wav"));

        preferencias = Gdx.app.getPreferences("flappybird");                      //para armazenar preferencias.
        pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima", 0);       // para pegar a pontuação maxima e inicializar.

        camera = new OrthographicCamera();                                              //dizendo que a camera agora é ortografica.
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);        //instanciando a camera com as medidas.
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);          //dizendo que o viewPort é uma stretch com as medidas do parametro.

    }

    private void verificarEstadoJogo() {

        boolean toqueTela = Gdx.input.justTouched();            //verifica se tocou na tela.

        if(estadoJogo == 0)   {                              //designa o estado inicial do jogo e muda com o toque na tela apenas uma vez.

            posicaoCoinHorizontal = larguraDispositivo / 2;

            if (Gdx.input.justTouched()) {                   //impulsiona pra cima se tocar na tela.
                gravidade = -15;                             //aplica a gravidade
                estadoJogo = 1;                              //muda o estado do jogo
                somVoando.play();                            //imprime o som qnd toca na tela.
            }
        } else if (estadoJogo == 1){                         //se o estado do jogo foi trocado no metodo de cima, deixa tocar mais vezes pra voar

            if (Gdx.input.justTouched()) {                   //impulsiona pra cima se tocar na tela.
                gravidade = -15;                             //aplica a gravidade.
                somVoando.play();                            //imprime o som qnd toca na tela.
            }

            posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;       //movimentação e velocidade do cano que vai vir na direção do player.

            posicaoCoinHorizontal -= Gdx.graphics.getDeltaTime() * 200;       //movimentação e velocidade da moeda

            if(posicaoCoinHorizontal < - ouroCoin.getWidth())
            {
                posicaoCoinHorizontal = larguraDispositivo;
                posicaoCoinVertical = random.nextInt(200) - 200;
            }

            if(posicaoCoinHorizontal < - prataCoin.getWidth())
            {
                posicaoCoinHorizontal = larguraDispositivo;
                posicaoCoinVertical = random.nextInt(200) - 200;
            }


            if(posicaoCanoHorizontal < - canoBaixo.getWidth()){               //ve a largura da tela pra avançar dps que acabar a tela.
                posicaoCanoHorizontal = larguraDispositivo;
                posicaoCanoVertical = random.nextInt(400) -200;            //randomiza os espaçamentos.
                passouCano = false;                                           //volta o cano pra false.
            }

            if (posicaoInicialVerticalPassaro > 0 || toqueTela)                               //associa o touch com a gravidade.
                posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;

            gravidade++;   //incrementa a gravidade.

        } else if (estadoJogo == 2){           //se o estado do jogo for = a 2.

            if(pontos > pontuacaoMaxima){
                pontuacaoMaxima = pontos;                                                 //pontuação maxima é igual pontos.
                preferencias.putInteger("pontuacaoMaxima", pontuacaoMaxima);           //colocando no preference pra guardar a variavel.
            }

            posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime() * 500;  //faz o passaro voltar pra tras qnd bate.

            if(toqueTela){                                                  //se tocar na tela.
                estadoJogo = 0;                                             //o estado do jogo volta pra 0.
                pontos = 0;                                                 //pontos vai pra 0.
                gravidade = 0;                                              //gravidade vai para 0.
                posicaoHorizontalPassaro = 0;                               //para n aparecer o passaro no lugar errado.
                posicaoInicialVerticalPassaro = alturaDispositivo / 2;      //passarinho volta ao ponto inicial.
                posicaoCanoHorizontal = larguraDispositivo;                 //volta os canos.
            }
        }
    }

    private void desenharTexturas() {

        batch.setProjectionMatrix(camera.combined);      //combinando o tamanho da camera com o tamanho do aparelho.


        batch.begin();   //inicializa a execução.



        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);          //instancia o fundo no celular utilizando o tamanho da tela passado como parametro.
        batch.draw(passaros[(int) variacao], 50 + posicaoHorizontalPassaro, posicaoInicialVerticalPassaro);    //instacia o passaro no celular com a posição dele e animação.

        batch.draw(canoBaixo, posicaoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espaçoEntreCanos / 2 + posicaoCanoVertical);  //instancia o cano na tela com espaço entre eles.
        batch.draw(canoTopo, posicaoCanoHorizontal, alturaDispositivo / 2 + espaçoEntreCanos / 2 + posicaoCanoVertical);                           //instancia o cano na tela com espaço entre eles.

        batch.draw(ouroCoin, posicaoCoinHorizontal, alturaDispositivo / 2 + posicaoCoinVertical);
        batch.draw(prataCoin, posicaoCoinHorizontal , alturaDispositivo / 2 + posicaoCoinVertical + 200);

        textoPontuacao.draw(batch,String.valueOf(pontos), larguraDispositivo / 2, alturaDispositivo - 100);  //n tem batch no começo pq esta vindo de um bitmapFont e desenha a pontuação na tela.

        if(estadoJogo == 0){
            batch.draw(flappyLogo, larguraDispositivo / 2 +200 - flappyLogo.getWidth(), alturaDispositivo / 2);
            textoIniciar.draw(batch, "TOQUE NA TELA PARA INICIAR!", larguraDispositivo / 2 -200, alturaDispositivo / 2 - flappyLogo.getHeight() / 2);               //inicia o escrito de reiniciar na tela com os parametros passados
        }

        if(estadoJogo == 2){                 //se o estado do jogo for 2
            batch.draw(gameOver, larguraDispositivo / 2 +200 - gameOver.getWidth(), alturaDispositivo / 2);                                                               // desenha o game over na tela com os parametros passados
            textoReiniciar.draw(batch, "TOQUE NA TELA PARA REINICIAR!", larguraDispositivo / 2 -250, alturaDispositivo / 2 - gameOver.getHeight() / 2);               //inicia o escrito de reiniciar na tela com os parametros passados
            textoMelhorPontuacao.draw(batch, "SUA MELHOR PONTUAÇÃO É:" + pontuacaoMaxima + "PONTOS", larguraDispositivo / 2 -250, alturaDispositivo / 2 - gameOver.getHeight() * 2);      //inicia o escrito de melhor pontuação com os parametros passados
        }

        batch.end();   //termina a execução.

    }

    @Override
    public void resize(int width, int height){            //metodo criado para ajustar o tamanho da tela.
        viewport.update(width, height);                   //viewport sempre da um update pra ser o melhor tamanho pro aparelho.
    }

    private void validarPontos() {

        if(posicaoCanoHorizontal < 50 - passaros[0].getWidth()){                  //condicional ao passar do cano.
           if(!passouCano){                                                       // verifica se passou do cano.
               pontos++;                                                          //incrementa os pontos.
               passouCano = true;                                                 //se passou do cano, agora é verdadeiro.
               somPontuacao.play();                                               //imprime o som pontuação ao passar do cano.
           }
        }

        variacao += Gdx.graphics.getDeltaTime() * 10;
        if (variacao > 3)                                         //mexe com a variação para mudar a animação.
            variacao = 0;
    }
    private void detectarColisão() {

        circuloPassaro.set(50 + passaros[0].getWidth() / 2, posicaoInicialVerticalPassaro + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2);                                     //associando o circulo do collider ao passaro.
        retanguloCanoBaixo.set(posicaoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espaçoEntreCanos / 2 + posicaoCanoVertical, canoBaixo.getWidth(), canoBaixo.getHeight());     //associando o retangulo do collider ao cano baixo.
        retanguloCanoCima.set(posicaoCanoHorizontal, alturaDispositivo / 2 + espaçoEntreCanos / 2 + posicaoCanoVertical, canoTopo.getWidth(), canoTopo.getHeight() );                               //associando o retangulo do collider ao cano topo.
        //circuloCoinOuro.set(ouroCoin.getWidth(), );
        //circuloCoinPrata.set();

        boolean colisaoCanoCima = Intersector.overlaps(circuloPassaro, retanguloCanoCima);           //se bateu ou nao no cano.
        boolean colisaoCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);         //se bateu ou nao no cano.

        boolean colisaoCoinPrata = Intersector.overlaps(circuloPassaro, circuloCoinPrata);           //se bateu ou nao na moeda.
        boolean colisaoCoinOuro = Intersector.overlaps(circuloPassaro, circuloCoinOuro);             //se bateu ou nao na moeda.

        if(colisaoCanoBaixo || colisaoCanoCima){                      //mensagem de bateu no cano.
            Gdx.app.log("log", "Colidiu");
           if(estadoJogo == 1) {
               somColisão.play();                                        //imprime o som colisão ao colidir
               estadoJogo = 2;                                           //muda para o estado dois que é game over.
           }
        }

        if(colisaoCoinOuro){               //se colidir com a moeda
            somCoin.play();                //da play no som
            pontos = pontos + 10;          //incrementa os pontos
        }
        if(colisaoCoinPrata){             //se colidir com a moeda
            pontos = pontos + 5;          //incremente os pontos
            somCoin.play();               // da play no som
        }
    }
}
