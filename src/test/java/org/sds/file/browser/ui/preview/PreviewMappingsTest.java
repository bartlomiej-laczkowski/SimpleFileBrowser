package org.sds.file.browser.ui.preview;

import org.mockito.Mockito;
import org.sds.file.browser.core.IFile;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PreviewMappingsTest {

	@Test
	public void testBrowserMappings() {
		IFile mockFile1 = Mockito.mock(IFile.class);
		Mockito.when(mockFile1.getName()).thenReturn("index.html");
		IFile mockFile2 = Mockito.mock(IFile.class);
		Mockito.when(mockFile2.getName()).thenReturn("index.HTML");
		IFile mockFile3 = Mockito.mock(IFile.class);
		Mockito.when(mockFile3.getName()).thenReturn("index.htm");
		IFile mockFile4 = Mockito.mock(IFile.class);
		Mockito.when(mockFile4.getName()).thenReturn("some.xml");
		Assert.assertEquals(PreviewMappings.getMapping(mockFile1), PreviewMappings.PreviewType.BROWSER);
		Assert.assertEquals(PreviewMappings.getMapping(mockFile2), PreviewMappings.PreviewType.BROWSER);
		Assert.assertEquals(PreviewMappings.getMapping(mockFile3), PreviewMappings.PreviewType.BROWSER);
		Assert.assertEquals(PreviewMappings.getMapping(mockFile4), PreviewMappings.PreviewType.BROWSER);
	}
	
	@Test
	public void testTextMappings() {
		IFile mockFile1 = Mockito.mock(IFile.class);
		Mockito.when(mockFile1.getName()).thenReturn("text.txt");
		IFile mockFile2 = Mockito.mock(IFile.class);
		Mockito.when(mockFile2.getName()).thenReturn("text.TXT");
		IFile mockFile3 = Mockito.mock(IFile.class);
		Mockito.when(mockFile3.getName()).thenReturn("some-rtf.rtf");
		IFile mockFile4 = Mockito.mock(IFile.class);
		Mockito.when(mockFile4.getName()).thenReturn("batman.bat");
		IFile mockFile5 = Mockito.mock(IFile.class);
		Mockito.when(mockFile5.getName()).thenReturn("script.sh");
		IFile mockFile6 = Mockito.mock(IFile.class);
		Mockito.when(mockFile6.getName()).thenReturn("logme.log");
		IFile mockFile7 = Mockito.mock(IFile.class);
		Mockito.when(mockFile7.getName()).thenReturn("php.ini");
		Assert.assertEquals(PreviewMappings.getMapping(mockFile1), PreviewMappings.PreviewType.TEXT);
		Assert.assertEquals(PreviewMappings.getMapping(mockFile2), PreviewMappings.PreviewType.TEXT);
		Assert.assertEquals(PreviewMappings.getMapping(mockFile3), PreviewMappings.PreviewType.TEXT);
		Assert.assertEquals(PreviewMappings.getMapping(mockFile4), PreviewMappings.PreviewType.TEXT);
		Assert.assertEquals(PreviewMappings.getMapping(mockFile5), PreviewMappings.PreviewType.TEXT);
		Assert.assertEquals(PreviewMappings.getMapping(mockFile6), PreviewMappings.PreviewType.TEXT);
		Assert.assertEquals(PreviewMappings.getMapping(mockFile7), PreviewMappings.PreviewType.TEXT);
	}
	
	@Test
	public void testImageMappings() {
		IFile mockFile1 = Mockito.mock(IFile.class);
		Mockito.when(mockFile1.getName()).thenReturn("biempi.bmp");
		IFile mockFile2 = Mockito.mock(IFile.class);
		Mockito.when(mockFile2.getName()).thenReturn("icon.TIFF");
		IFile mockFile3 = Mockito.mock(IFile.class);
		Mockito.when(mockFile3.getName()).thenReturn("other-icon.ico");
		IFile mockFile4 = Mockito.mock(IFile.class);
		Mockito.when(mockFile4.getName()).thenReturn("111.jpeg");
		IFile mockFile5 = Mockito.mock(IFile.class);
		Mockito.when(mockFile5.getName()).thenReturn("abc.jpg");
		IFile mockFile6 = Mockito.mock(IFile.class);
		Mockito.when(mockFile6.getName()).thenReturn("img.gif");
		IFile mockFile7 = Mockito.mock(IFile.class);
		Mockito.when(mockFile7.getName()).thenReturn("SOME.png");
		Assert.assertEquals(PreviewMappings.getMapping(mockFile1), PreviewMappings.PreviewType.IMAGE);
		Assert.assertEquals(PreviewMappings.getMapping(mockFile2), PreviewMappings.PreviewType.IMAGE);
		Assert.assertEquals(PreviewMappings.getMapping(mockFile3), PreviewMappings.PreviewType.IMAGE);
		Assert.assertEquals(PreviewMappings.getMapping(mockFile4), PreviewMappings.PreviewType.IMAGE);
		Assert.assertEquals(PreviewMappings.getMapping(mockFile5), PreviewMappings.PreviewType.IMAGE);
		Assert.assertEquals(PreviewMappings.getMapping(mockFile6), PreviewMappings.PreviewType.IMAGE);
		Assert.assertEquals(PreviewMappings.getMapping(mockFile7), PreviewMappings.PreviewType.IMAGE);
	}
	
}
