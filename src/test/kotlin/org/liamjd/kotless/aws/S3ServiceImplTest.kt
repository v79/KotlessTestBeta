package org.liamjd.kotless.aws

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

internal class S3ServiceImplTest {

	@Test
	fun `can get S3 objects from specified bucket`() {
		//setup
		val service = S3ServiceImpl()

		//execute
		val objectList = service.getBucketListing("www.liamjd.org", "assets/")

		// verify
		assertNotNull(objectList) {
			assertNotEquals(0,objectList.size)
			objectList.forEach { print(it); println(" (${it.fileSizeKb}Kb)") }
		}
	}

	@Test
	fun `gracefully handles an invalid request for a non-existing prefix`() {
		//setup
		val service = S3ServiceImpl()

		//execute
		val objectList = service.getBucketListing("www.liamjd.org", "greeply/")

		// verify
		assertNotNull(objectList) {
			assertEquals(0,objectList.size)
		}
	}

	@Test
	fun `gracefully handles an invalid request for a non-existing bucket`() {
		//setup
		val service = S3ServiceImpl()

		//execute
		val objectList = service.getBucketListing("www.greepkly.org", "assets/")

		// verify
		assertNotNull(objectList) {
			assertEquals(0,objectList.size)
		}
	}

	@Test
	fun `returns a list of files from the bucket root when an empty prefix is supplied`() {
		//setup
		val service = S3ServiceImpl()

		//execute
		val objectList = service.getBucketListing("src.liamjd.org", "")

		// verify
		assertNotNull(objectList) {
			assertNotEquals(0,objectList.size)
		}
	}
}
