<?php
header ("content-type: application/json; charset=utf-8");

    $filename = "./data/ShimmerData5";
    displayTXTList($filename);

    function displayTXTList($fileName) {
        $json = "{vitals:{vital:[";
        $i = 0;

        if(file_exists($fileName)) {
            $file = fopen($fileName,'r');
            while(!feof($file)) {
                $line = fgets($file);
                $tok = strtok($line, ";");
                $tok = strtok(";");
                $tok = strtok(";");

                $time_str = strtok(";");
                $time_float = floatval($time_str);
                $time_int = round($time_float * 1000);

                $volt_str = strtok(";");
                $volt_float = floatval($volt_str);
                $volt_int = round($volt_float * 10000000);

                while ($tok !== false) {
                    $tok = strtok(";");
                }
                $vitals = "{vid:$i,ip_addr:0,timestamp:$time_int,sensor_type:0,value_type:0,value:$volt_int},";
                $json = $json . $vitals;
                $i = $i + 1;
            }
            $json = substr($json, 0, -1);
            $json = $json . "]}}";
            echo $json;
            fclose($file);
        } else {
            echo('File '.$fileName.' does not exist');
        }
    }

?>