<?php 

	$name = $_GET['nama'];
	$city = $_GET['kota'];

	echo json_encode(array('nama' => $name, 'kota' => $city));

?>