<?php 

	require_once('connection.php');

	$dari = $_POST['dari'];
	$ke = $_POST['ke'];
	
	$str_query = "SELECT * FROM transaksi WHERE(tanggal >= '$dari') AND (tanggal <= '$ke') ORDER BY id_transaksi DESC"; 
	/*echo $str_query; 
	die();*/
	$sql = mysqli_query($conn, $str_query);
	
	$hasil = array();
		while ($baris = mysqli_fetch_array($sql)) {
			$tanggal = date_format(date_create($baris['tanggal']), "d/m/Y");

			array_push($hasil, array(
				'id_transaksi' => $baris['id_transaksi'],
				'status' 	   => $baris['status'],
				'jumlah' 	   => $baris['jumlah'],
				'keterangan'   => $baris['keterangan'],
				'tanggal'      => $tanggal
		));
	}

	$str_query = "SELECT(SELECT SUM(jumlah) FROM `transaksi` WHERE `status`='masuk' AND ((tanggal >= '$dari') AND (tanggal <= '$ke'))) AS income, (SELECT SUM(jumlah) FROM `transaksi` WHERE `status`='keluar' AND ((tanggal >= '$dari') AND (tanggal <= '$ke'))) AS outcome";
	$sql = mysqli_query($conn,$str_query);
	
	while ($baris = mysqli_fetch_array($sql)) {
		$masuk  = $baris['income'];
		$keluar = $baris['outcome'];
	}

	echo json_encode(array(
		'masuk'  => $masuk,
		'keluar' => $keluar,
		'saldo'   => ($masuk - $keluar),
		'hasil'   => $hasil
	));
?>
