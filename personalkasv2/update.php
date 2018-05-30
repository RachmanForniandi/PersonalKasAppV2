<?php 
	
	require_once('connection.php');

	$id_transaksi = $_POST['id_transaksi'];
	$status = $_POST['status'];
	$jumlah = $_POST['jumlah'];
	$keterangan = $_POST['keterangan'];
	$tanggal = date('Y-m-d');
	
	$str_query = "UPDATE transaksi SET status='$status', jumlah ='$jumlah', keterangan='$keterangan', tanggal='$tanggal' WHERE id_transaksi='$id_transaksi'";
	
	$sql = mysqli_query($conn, $str_query);

	if ($sql) {
		echo json_encode(array('response' => 'success'));
	}else{
		echo json_encode(array('response' => 'failed'));
	}

?>