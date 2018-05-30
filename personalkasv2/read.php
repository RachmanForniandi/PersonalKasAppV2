<?php 

	require_once('connection.php');
	
	$sql = mysqli_query($conn, "SELECT * FROM transaksi ORDER BY id_transaksi DESC");
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

	$sql = mysqli_query($conn,"SELECT(SELECT SUM(jumlah) FROM `transaksi` WHERE `status`='masuk')AS income, (SELECT SUM(jumlah) FROM `transaksi` WHERE `status`='keluar')AS outcome"
	);
	
	while ($baris = mysqli_fetch_array($sql)) {
		$masuk = $baris['income'];
		$keluar = $baris['outcome'];
	}

	echo json_encode(array(
		'masuk'  => $masuk,
		'keluar' => $keluar,
		'saldo'   => ($masuk - $keluar),
		'hasil'   => $hasil
	));
?>
