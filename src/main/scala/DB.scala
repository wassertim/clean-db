import model.ImageFile

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession

import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation

object DB {


  val db = Database.forURL("jdbc:mysql://localhost:3306/dbs?useEncoding=true&characterEncoding=UTF-8&allowMultiQueries=true",
    user = "notonlywhite",
    password = "notonlywhite",
    driver = "com.mysql.jdbc.Driver")

  def getUsedBrandFiles = db.withDynSession {
    implicit val getResult = GetResult(r => ImageFile(r.<<, r.<<))
    sql"""
      select
        im.image_id,
        im.file_path
      from images im
      inner join brand_images pi on pi.image_id = im.image_id
      where pi.brand_id in (select prod.id from brands prod);
    """.as[ImageFile].list
  }

  def getUsedCategoryFiles = db.withDynSession {
    implicit val getResult = GetResult(r => ImageFile(r.<<, r.<<))
    sql"""
      select
        im.image_id,
        im.file_path
      from images im
      inner join category_images pi on pi.image_id = im.image_id
      where pi.category_id in (select prod.category_id from categories prod);
    """.as[ImageFile].list
  }
  def deleteCategoryImage(imageId: Int) = db.withDynSession {
    sqlu"""
      delete from images where image_id = ${imageId};
      delete from category_images where image_id = ${imageId}
    """.execute
  }
  def deleteBrandImage(imageId: Int) = db.withDynSession {
    sqlu"""
      delete from images where image_id = ${imageId};
      delete from brand_images where image_id = ${imageId}
    """.execute
  }
  def deleteProductImage(imageId: Int) = db.withDynSession {
    sqlu"""
      delete from images where image_id = ${imageId};
      delete from product_images where image_id = ${imageId};
    """.execute
    
    /*
    delete from brand_images where image_id = ${imageId};
      delete from category_images where image_id = ${imageId};
     */
  }

  def getUsedProductFiles = db.withDynSession {
    implicit val getResult = GetResult(r => ImageFile(r.<<, r.<<))
    sql"""
      select
        im.image_id,
        im.file_path
      from images im
      inner join product_images pi on pi.image_id = im.image_id
      where pi.product_id in (select prod.id from products prod);
    """.as[ImageFile].list
  }

  def getUnusedFiles = db.withDynSession {
    implicit val getResult = GetResult(r => ImageFile(r.<<, r.<<))
    sql"""
      select
        img.image_id,
        img.file_path
      from
        images img
      where img.image_id not in (
        select im.image_id from images im
        inner join product_images pi on pi.image_id = im.image_id
        where pi.product_id in (select prod.id from products prod)
      ) and file_path like '%productimages/%';
    """.as[ImageFile].list
  }
}
