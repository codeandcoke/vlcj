package org.sfsoft.holavlcj;

import java.awt.EventQueue;

import javax.swing.JFrame;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.sfsoft.holavlcj.filters.VideoFilter;

/**
 * Reproductor de videos con la única funcionalidad de reproducir o pausar
 * Hay que tener en cuenta que el componente que se encarga de la reproducción sólo se puede integrar 
 * contenedores que deriven de JFrame o JWindow, como por ejemplo un JFrame o un JInternalFrame. No se puede
 * añadir a un JPanel
 * Es necesario disponer de las librerias de VLC que vienen con la aplicación de reproducción de video VLC
 * 
 * @author Santiago Faci
 * @version 1.0
 */
public class HolaVLCJ {

	private JFrame frmReproductorDeVideo;
	private JPanel panelBotones;
	private JButton btPlay;
	private JInternalFrame internalFrame;
	private JMenuBar menuBar;
	private JMenu mnArchivo;
	private JMenuItem mntmAbrir;
	private JMenu mnAyuda;
	private JMenuItem mntmConfigurar;
	
	// Componente que permite gestionar los ficheros de video
	private EmbeddedMediaPlayerComponent mediaPlayer;
	
	// Ubicación de las librerías de VLC
	private static final String LIB_VLC = "C:\\Archivos de programa\\VideoLAN\\VLC\\";
	private enum Estado {PLAY, PAUSE, STOP};
	private Estado estado;
	private File ficheroVideo;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HolaVLCJ window = new HolaVLCJ();
					window.frmReproductorDeVideo.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public HolaVLCJ() {
		inicializarVLCJ();
		initialize();
		iniciarVideo();
	}
	
	/*
	 * Reproduce un video seleccionado por el usuario
	 */
	private void abrirVideo() {
		
		JFileChooser jfc = new JFileChooser();
		jfc.addChoosableFileFilter(new VideoFilter());
		jfc.setAcceptAllFileFilterUsed(false);
		if (jfc.showOpenDialog(null) == JFileChooser.CANCEL_OPTION)
			return;
		
		ficheroVideo = jfc.getSelectedFile();
		estado = Estado.STOP;
		reproducirVideo();
	}
	
	/*
	 * Inicializa el componente que reproduce los videos
	 */
	private void iniciarVideo() {
	
		internalFrame.setContentPane(mediaPlayer);
		internalFrame.setVisible(true);
		
		estado = Estado.STOP;
	}
	
	/*
	 * Reproduce/pausa el archivo de video
	 */
	private void reproducirVideo() {
		
		// El reproductor está parado
		if (estado == Estado.STOP) {
			internalFrame.setTitle(ficheroVideo.getName());
			mediaPlayer.getMediaPlayer().playMedia(ficheroVideo.getAbsolutePath());
			estado = Estado.PLAY;	
			btPlay.setText("||");
		}
		// El reproductor está pausado
		else if (estado == Estado.PAUSE) {
			mediaPlayer.getMediaPlayer().play();
			estado = Estado.PLAY;
			btPlay.setText("||");
		}
		// El reproductor está reproduciendo
		else {
			mediaPlayer.getMediaPlayer().pause();
			estado = Estado.PAUSE;
			btPlay.setText(">");
		}
	}
	
	/*
	 * Inicializa la libreria VLCJ, cargando las librerías del sistema e instanciando el reproductor
	 */
	private void inicializarVLCJ() {
		
		cargaLibreria();
		mediaPlayer = new EmbeddedMediaPlayerComponent();
	}
	
	/*
	 * Carga la libreria libvlc.dll en la ruta indicada
	 * Es necesario instalar la aplicación VLC 2.X 
	 */
	
	private void cargaLibreria() {
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), LIB_VLC);
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frmReproductorDeVideo = new JFrame();
		frmReproductorDeVideo.setTitle("Reproductor de video");
		frmReproductorDeVideo.setBounds(100, 100, 450, 300);
		frmReproductorDeVideo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panelBotones = new JPanel();
		frmReproductorDeVideo.getContentPane().add(panelBotones, BorderLayout.SOUTH);
		
		btPlay = new JButton(">");
		btPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reproducirVideo();
			}
		});
		panelBotones.add(btPlay);
		
		internalFrame = new JInternalFrame("");
		internalFrame.setFrameIcon(null);
		internalFrame.setBorder(null);
		frmReproductorDeVideo.getContentPane().add(internalFrame, BorderLayout.CENTER);
		
		menuBar = new JMenuBar();
		frmReproductorDeVideo.setJMenuBar(menuBar);
		
		mnArchivo = new JMenu("Archivo");
		menuBar.add(mnArchivo);
		
		mntmAbrir = new JMenuItem("Abrir . . .");
		mntmAbrir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abrirVideo();
			}
		});
		mnArchivo.add(mntmAbrir);
		
		mntmConfigurar = new JMenuItem("Configurar");
		mnArchivo.add(mntmConfigurar);
		
		mnAyuda = new JMenu("Ayuda");
		menuBar.add(mnAyuda);
	}

}
