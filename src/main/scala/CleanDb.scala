import java.io.{FileNotFoundException, File, FileInputStream, FileOutputStream}
import java.nio.channels.FileChannel
import java.nio.file.{Paths, Files}

import model.ImageFile
import org.apache.commons.io.FileUtils

object CleanDb {
  val dataPath = "/Users/tim/data/digitalband/data/images/originals"
  val tempPath = "/Users/tim/data/digitalband/temp"
  val cachePath = "/Users/tim/data/digitalband/data/images/cache"

  def deleteFile(filePath: String): Unit = {
    Files.deleteIfExists(Paths.get(dataPath, filePath))
  }

  def deleteFiles(files: List[ImageFile]): Unit = {
    files.foreach {
      imageFile =>
        println(s"delete file: ${imageFile.path}")
        deleteFile(imageFile.path)
    }
  }

  def moveOnlyUsedFilesToTemp(usedFiles: List[ImageFile], deleteImage: Int => Unit) = {
    usedFiles.foreach {
      file =>
        println(s"move file: ${file.path}")
        try {
          FileUtils.copyFile(Paths.get(dataPath, file.path).toFile, Paths.get(tempPath, file.path).toFile)
        } catch {
          case ex:FileNotFoundException =>
            println(s"File not found: ${file.path}")
            deleteImage(file.id)
            println(s"File deleted from database: ${file.path}")
        }
    }
  }

  def deleteAllFiles() {
    FileUtils.deleteDirectory(Paths.get(dataPath).toFile)
    FileUtils.deleteDirectory(Paths.get(cachePath).toFile)
  }

  def moveOnlyUsedFilesBack(): Unit = {
    FileUtils.copyDirectory(Paths.get(tempPath).toFile, Paths.get(dataPath).toFile)
  }

  

  def main(args: Array[String]) {
    //val unusedFiles = DB.getUnusedFiles
    //deleteFiles(unusedFiles)
    println(s"move product images =======================================")
    val usedProductFiles = DB.getUsedProductFiles
    println(s"product files: ${usedProductFiles.size}")
    moveOnlyUsedFilesToTemp(usedProductFiles, DB.deleteProductImage)

    println(s"move brand images =======================================")
    val usedBrandFiles = DB.getUsedBrandFiles
    println(s"product files: ${usedBrandFiles.size}")
    moveOnlyUsedFilesToTemp(usedBrandFiles, DB.deleteBrandImage)


    println(s"move category images =======================================")
    val usedCategoryFiles = DB.getUsedCategoryFiles
    println(s"product files: ${usedCategoryFiles.size}")
    moveOnlyUsedFilesToTemp(usedCategoryFiles, DB.deleteCategoryImage)
    println("delete all files")
    deleteAllFiles()
    println("move all files back")
    moveOnlyUsedFilesBack()
    //TODO: delete records in images
    //TODO: delete records in product_images
    //TODO: delete records in category_images
  }

}