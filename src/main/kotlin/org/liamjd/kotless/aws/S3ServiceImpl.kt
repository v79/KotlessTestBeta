package org.liamjd.kotless.aws

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.ResponseBytes
import software.amazon.awssdk.core.ResponseInputStream
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectResponse
import software.amazon.awssdk.services.s3.model.ListObjectsRequest
import software.amazon.awssdk.services.s3.model.S3Exception
import java.nio.charset.Charset
import java.time.Instant


class S3ServiceImpl : S3Service {

	private val AWS_ACCESS_KEY = System.getenv("AWS_ACCESS_KEY_PYLON")
	private val AWS_SECRET_KEY = System.getenv("AWS_SECRET_KEY_PYLON")
	private val awsCreds = AwsBasicCredentials.create(AWS_ACCESS_KEY, AWS_SECRET_KEY )
	private val s3 = S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(awsCreds)).build()

	override fun getBucketListing(bucket: String, prefixFolder: String): List<S3File> {

		try {
			val folderRequest = ListObjectsRequest.builder().bucket(bucket).delimiter("/").prefix(prefixFolder).build()
			val folderResponse = s3.listObjects(folderRequest)
			val contents = folderResponse.contents()
			val filelist = mutableSetOf<S3File>()

			contents.forEach {
				filelist.add(S3File(
					s3key = it.key(),
					fileName = it.key().substringAfter(prefixFolder),
					fileSize = it.size(),
					lastModified = if(it.lastModified() != null) { it.lastModified()} else { Instant.now()},
					isVirtualFolder = false
				))
			}
			folderResponse.commonPrefixes().forEach {
				// folders are not real on S3, I'm just faking it based on common prefixes
				filelist.add(S3File(
					s3key = it.prefix(),
					fileName = it.prefix(),
					fileSize = 0L,
					lastModified = Instant.now(),
					isVirtualFolder = true
				))
			}
			return filelist.toList()
		} catch (e: S3Exception) {
			println(e.awsErrorDetails().errorMessage())
		}
		return emptyList()
	}

	override fun loadTextFile(bucket: String, s3key: String ): String {
		try {
			val getObjectRequest = GetObjectRequest.builder()
				.bucket(bucket)
				.key(s3key)
				.build()
			val result: ResponseBytes<GetObjectResponse> = s3.getObjectAsBytes(getObjectRequest)
			return result.asString(Charset.defaultCharset())
		} catch (e: S3Exception) {
			println(e.message)
		}

		return ""
	}
}

data class S3File(val s3key: String, val fileName: String, val fileSize: Long, val lastModified: Instant, val isVirtualFolder: Boolean = false) {
	val fileSizeKb: Long
		get() = fileSize / 1024
}


class FileNode(val key: String, val filename: String) {
	var isFolder: Boolean = false
	var parent: FileNode? = null
	var children: Set<FileNode> = emptySet()

	override fun toString(): String {
		return "Key: $key : $filename [parent=$parent, childCount=${children.size}"
	}
}
