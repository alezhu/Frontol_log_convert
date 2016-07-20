package ru.alezhu.frontol_log_convert.test;

import org.junit.Before;
import org.junit.Test;
import ru.alezhu.frontol_log_convert.frontol.tables.MaterialTable;

import static org.junit.Assert.*;

public class BaseRowTest {

	private MaterialTable.Row row1;
	private MaterialTable.Row row2;
	private MaterialTable.Row row3;

	@Before
	public void setUp() throws Exception {
		this.row1 = new MaterialTable.Row();
		this.row2 = new MaterialTable.Row();
		this.row3 = new MaterialTable.Row();

		this.row1.ID = this.row2.ID = 1;
		this.row1.CODE = this.row2.CODE = 343;
		this.row3.ID = 2;
		this.row3.CODE = 343;
	}

	@Test
	public void testHashCode() {
		assertEquals(this.row1, this.row2);
		assertNotEquals(this.row1, this.row3);
		assertEquals(this.row1.hashCode(), this.row2.hashCode());
		assertNotEquals(this.row1.hashCode(), this.row3.hashCode());
	}

	@Test
	public void testEqualsObject() {
		assertTrue(this.row1.equals(this.row2));
		assertFalse(this.row1.equals(this.row3));
		assertFalse(this.row1 == this.row2);
		assertFalse(this.row1 == this.row3);
	}

}
