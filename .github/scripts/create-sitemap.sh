#!/bin/bash
 
set -e

echo '<?xml version="1.0" encoding="UTF-8"?>'
echo '<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">'
aws s3 ls s3://$S3_BUCKET/docs --recursive > docs-content.log
grep -e " docs/[^0-9|next].*index.html$" docs-content.log > $BASEDIR/logs/main-pages.log
while read line; do
  PAGE_DATE=$(echo $line | cut -d' ' -f1)
  PAGE_PATH=$(echo $line | cut -d' ' -f4)
  echo "<url><loc>https://arrow-kt.io/${PAGE_PATH}</loc><lastmod>${PAGE_DATE}</lastmod></url>"
done < $BASEDIR/logs/main-pages.log
echo '</urlset>'
