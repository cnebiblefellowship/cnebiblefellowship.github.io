<?php
$file = 's/r21.mp3'; // 文件路径

if (file_exists($file)) {
    header('Content-Description: File Transfer');
    header('Content-Type: audio/mpeg');
    header('Content-Disposition: attachment; filename="罗马书2章（1）.mp3"');
    header('Expires: 0');
    header('Cache-Control: must-revalidate');
    header('Pragma: public');
    header('Content-Length: ' . filesize($file));
    readfile($file);
    exit;
} else {
    echo '文件不存在。';
}
?>
