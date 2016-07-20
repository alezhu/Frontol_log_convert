package ru.alezhu.frontol_log_convert.test;

import org.junit.Before;
import org.junit.Test;
import ru.alezhu.frontol_log_convert.frontol.tables.base.CompositeKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


public class CompositeKeyTest {

	private CompositeKey compositeKey1;
	private CompositeKey compositeKey2;
	private CompositeKey compositeKey3;

	@Before
	public void setUp() throws Exception {
		this.compositeKey1 = new CompositeKey();
		this.compositeKey1.addValue("ID", 1);
		this.compositeKey2 = new CompositeKey();
		this.compositeKey2.addValue("ID", 1);
		this.compositeKey3 = new CompositeKey();
		this.compositeKey3.addValue("ID", 3);
	}

	@Test
	public void testEqual() {
		assertEquals(this.compositeKey1, this.compositeKey2);
		assertNotEquals(this.compositeKey1, this.compositeKey3);
	}

	@Test
	public void testHashCode() {
		assertEquals(this.compositeKey1.hashCode(), this.compositeKey2.hashCode());
		assertNotEquals(this.compositeKey1.hashCode(), this.compositeKey3.hashCode());
	}

}
