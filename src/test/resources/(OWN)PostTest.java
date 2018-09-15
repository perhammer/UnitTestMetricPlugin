package Postage.Postage;

import static org.junit.Assert.*;

import org.junit.Test;

import static org.mockito.Mockito.*;

import java.io.File;

import org.junit.Test;
import org.mockito.Mock;

public class PostTest {
	/* Test using equivalence classes. 
	 * Takes the six possible outputs of the program and tests if given certain values the output is correct. 
	 * For example for Parcel Force the weight, depth and length added together must be over 90. 	 */
	
	@Test
	public void ParcelForcetest() {
		//Tests if height width and depth are over 90, Service correctly set as ParcelForce
		PostGUI PostTest = new PostGUI();
		assertEquals("ParcelForce", PostTest.Service(90, 90, 90, 1, 90, 90));
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void Specialtest() {
		//Tests if value is over 50, Service correctly set as Special 
		PostGUI PostTest = new PostGUI();
		assertEquals("Special", PostTest.Service(10, 10, 10, 1, 60, 90));
		System.out.print("Special test");
		
	}
	@Test
	public void FirstSignedtest() {
		//Tests if Service correctly set as First class Signed for
		PostGUI PostTest = new PostGUI();
		assertEquals("First class Signed for", PostTest.Service(10, 10, 10, 2, 33, 90));
		assertEquals("First class Signed for", PostTest.Service(20, 20, 20, 2, 44, 80));
	}
	
	@Test
	public void SecondSignedtest() {
		//Tests if Service is Second class signed for 
		PostGUI PostTest = new PostGUI();
		assertEquals("Second class Signed for", PostTest.Service(10, 10, 10, 3, 33, 90));
		int Time = 900; 
	}
	
	@Test
	public void Firstclasstest() {
		// Tests if value is under 20 and desired delivery is 2, Service is First class 
		
		PostGUI PostTest = new PostGUI();
		assertEquals("First class", PostTest.Service(10, 10, 10, 2, 19, 90));
	}
	
	@Test
	public void Secondclasstest() {
		//Tests if value is under 20 and desired delivery is 3, Service is Second class 
		PostGUI PostTest = new PostGUI();	

		assertEquals("Second class", PostTest.Service(10, 10, 10, 3, 19, 90));

		//Testing to see how external resource, in this case a file is represented in bytecode 
	
		
		}
	@Test 
	public void mockingtest() {
		PostGUI PostTest = mock(PostGUI.class);
		
		when(PostTest.Service(10, 10, 10, 3, 19, 90)).thenReturn("Secondclass");
		
		verify(PostTest, times(0)).Service(0, 0, 0, 0, 0, 0);
		
		
		
	}

	}


