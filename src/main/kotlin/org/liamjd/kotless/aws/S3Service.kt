package org.liamjd.kotless.aws

import kotlinx.serialization.json.Json
import software.amazon.awssdk.services.s3.model.S3Object

interface S3Service {

	fun getBucketListing(bucket: String, prefixFolder: String) : List<S3File>

	fun loadTextFile(bucket: String, s3key: String) : String
}
