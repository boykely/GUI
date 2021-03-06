import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class CamDialog extends JFrame implements Runnable,WindowListener,ActionListener
{
	private JFrame mainFrame;
	private JScrollPane scroll;
	private JLabel imageLabel;
	private JLabel mainLabel;
	private BufferedImage image;
	private JButton btn_take;
	private VideoCapture capture;
	private boolean takePic=false;
	private List _ImageLoadedlisteners;
	
	public CamDialog(JFrame frame,JLabel target ) throws IOException
	{
		_ImageLoadedlisteners=new ArrayList<>();
		mainFrame=frame;
		mainLabel=target;
		imageLabel=new JLabel();
		btn_take=new JButton("Prendre photos");btn_take.setName("pic");
		scroll=new JScrollPane(imageLabel);
		scroll.setPreferredSize(new Dimension(600,500));
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(scroll);
		add(btn_take);
		setPreferredSize(new Dimension(800,500));
		setResizable(false);
		pack();
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(this);
		btn_take.addActionListener(this);
	}
	public void addImageLoadedListener(ImageLoadedListener ill)
	{
		_ImageLoadedlisteners.add(ill);
	}
	public void removeImageLoadedListener(ImageLoadedListener ill)
	{
		_ImageLoadedlisteners.remove(ill);
	}
	public void LunchImageLoadedEvent(BufferedImage im)
	{
		ImageLoadedEvent e=new ImageLoadedEvent(this,im);
		Iterator listeners = _ImageLoadedlisteners.iterator();
        while( listeners.hasNext() ) 
        {
            ( (ImageLoadedListener) listeners.next() ).ImageLoaded(e);
        }
	}
	private void initCamera()
	{
		 capture=new VideoCapture();
		capture.open(0);
		if(!capture.isOpened())
		{
			System.err.println("cannot open cam device");
			return;
		}
		Mat fps=new Mat();
		capture.set(Videoio.CAP_PROP_FRAME_WIDTH,640);
		capture.set(Videoio.CAP_PROP_FRAME_HEIGHT,480);
		while(capture.read(fps))
		{		
			image=UtilsOpenCV.convertCVToTile(fps);
			imageLabel.setIcon(new ImageIcon(image));
			if(takePic)
			{				
				LunchImageLoadedEvent(image);
				takePic=!takePic;
			}
		}
		capture.release();
	}
	public BufferedImage getImage()
	{
		return image;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		initCamera();
	}
	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		if(capture!=null)
		{
			capture.release();
		}		
	}
	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		JButton btn=(JButton)e.getSource();
		if(btn.getName()=="pic")
		{
			takePic=!takePic;
			
		}
	}
}
