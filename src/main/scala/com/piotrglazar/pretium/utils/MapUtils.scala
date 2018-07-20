package com.piotrglazar.pretium.utils

object MapUtils {
  implicit class MapHelper[K, V](map: Map[K, List[V]]) {
    def singleValue(): Map[K, V] = {
      map.mapValues(_.head).map(identity)
    }
  }
}
