CREATE SCHEMA `chain` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin ;


SET character_set_client = utf8mb4 ;
DROP TABLE IF EXISTS `chain_did_property`;
CREATE TABLE `chain_did_property` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `did` varchar(34) COLLATE utf8mb4_bin NOT NULL,
  `did_status` tinyint(2) NOT NULL COMMENT 'status of did , ''1'' or ''0''',
  `public_key` varchar(66) COLLATE utf8mb4_bin NOT NULL COMMENT 'public key of did',
  `property_key` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT 'property key',
  `property_key_status` tinyint(2) NOT NULL COMMENT 'status of did property, ''1'' or ''0''',
  `property_value` text COLLATE utf8mb4_bin NOT NULL COMMENT 'property value',
  `txid` varchar(64) COLLATE utf8mb4_bin NOT NULL,
  `block_time` int(11) NOT NULL,
  `height` int(11) NOT NULL,
  `local_system_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='did information';


CREATE INDEX idx_chain_did_property_property_key ON chain_did_property (property_key);
CREATE INDEX idx_chain_did ON chain_did_property (did);

