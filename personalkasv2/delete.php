<?php 
	
	require_once('connection.php');

	$id_transaksi = $_POST['id_transaksi'];
	
	$sql = mysqli_query($conn, "DELETE FROM transaksi WHERE id_transaksi='$id_transaksi'");

	if ($sql) {
		echo json_encode(array('response' => 'success'));
	}else{
		echo json_encode(array('response' => 'failed'));
	}

?>