package io.jz.poodle

import akka.util.ByteString
import org.json4s.CustomSerializer
import org.json4s.JsonAST._


object ByteStringSerializer extends CustomSerializer[ByteString] (format => (
  { case JArray(bytes: List[_]) => ByteString(bytes.map(_.asInstanceOf[JInt].values.toByte).toArray) },
  { case byteString: ByteString => JArray(byteString.toList.map(b => JInt(BigInt(b.toInt)))) }
  ))
