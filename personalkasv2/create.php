<?php 
	
	require_once('connection.php');

	$status = $_POST['status'];
	$jumlah = $_POST['jumlah'];
	$keterangan = $_POST['keterangan'];
	$tanggal = date('Y-m-d');
	
	$sql = mysqli_query($conn, "INSERT INTO transaksi(status, jumlah, keterangan, tanggal) VALUES('$status','$jumlah','$keterangan','$tanggal')");

	if ($sql) {
		echo json_encode(array('response' => 'success'));
	}else{
		echo json_encode(array('response' => 'failed'));
	}

?>