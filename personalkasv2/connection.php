<?php

	define('HOST', 'localhost');
	define('USER', 'root');
	define('PASS', '');
	define('DB', 'db_personalkas_v2');

	$conn = mysqli_connect(HOST, USER, PASS, DB) OR DIE('cant connect to database');

	if ($conn) {
		echo "Yes, You're connected.";
	}else{
		echo "No, Please check again.";
	}

?>