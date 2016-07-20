package ru.alezhu.frontol_log_convert.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.alezhu.frontol_log_convert.DateTime;

import java.time.LocalDateTime;
import java.time.Month;

public class DateTimeTest {

	DateTime dt;

	@Before
	public void setUp() {
		this.dt = new DateTime("26.04.2016 13:53:38");
	}

	@Test
	public void test_getDate() {
		Assert.assertEquals(this.dt.getDate(), "26.04.2016");
	}

	@Test
	public void test_getTime() {
		Assert.assertEquals(this.dt.getTime(), "13:53:38");
	}

	@Test
	public void test_getDateTime() {
		final LocalDateTime value = this.dt.getDateTime();
		final LocalDateTime expect = LocalDateTime.of(2016, Month.APRIL, 26, 13, 53, 38);
		Assert.assertEquals(value, expect);
	}

}
