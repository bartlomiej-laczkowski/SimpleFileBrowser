package org.sds.file.browser.core.remote;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.Assert;

public class ClientConfigurationManagerTest {

	private static final String DUMMY_CONF_ID = "abcd-1234";
	
	@Mock
	private final ClientConfiguration mockConfiguration = Mockito.mock(ClientConfiguration.class);
	
	@BeforeTest
	public void setUp() {
		Mockito.when(mockConfiguration.getUniqueId()).thenReturn(DUMMY_CONF_ID);
	}
	
	@Test
	public void testIsEmptyOnLoad() {
		Assert.assertTrue(ClientConfigurationManager.INSTANCE.getAllConfigurations().isEmpty());
	}
	
	@Test(dependsOnMethods = {"testIsEmptyOnLoad"})
	public void testAddConfiguration() {
		ClientConfigurationManager.INSTANCE.addConfiguration(mockConfiguration);
		Assert.assertEquals(ClientConfigurationManager.INSTANCE.getAllConfigurations().size(), 1);
	}
	
	@Test(dependsOnMethods = {"testAddConfiguration"})
	public void testFindConfiguration() {
		ClientConfiguration configuration = ClientConfigurationManager.INSTANCE.findConfiguration(DUMMY_CONF_ID);
		Assert.assertEquals(ClientConfigurationManager.INSTANCE.getAllConfigurations().size(), 1);
		Assert.assertEquals(configuration, mockConfiguration);
	}
	
	@Test(dependsOnMethods = {"testFindConfiguration"})
	public void testRemoveConfiguration() {
		ClientConfigurationManager.INSTANCE.removeConfiguration(mockConfiguration.getUniqueId());
		Assert.assertEquals(ClientConfigurationManager.INSTANCE.getAllConfigurations().size(), 0);
	}
	
}
