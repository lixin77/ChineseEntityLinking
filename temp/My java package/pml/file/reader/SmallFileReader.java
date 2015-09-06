package pml.file.reader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import pml.file.FileException;

public class SmallFileReader implements FileReader
{
	public SmallFileReader(String filePath) 
	{
	}
	
	@Override

	public String Read() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object Scan(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object Scan(Object object, String delimer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String ReadLine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String ReadAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> ReadAllLine() {
		// TODO Auto-generated method stub
		return null;
	}

	public void Close()
	{
		
	}

	@Override
	public void Open() throws FileException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Open(String path) throws FileException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Open(String path, Charset charset) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean IsReady() throws FileException {
		// TODO Auto-generated method stub
		return false;
	}
}
