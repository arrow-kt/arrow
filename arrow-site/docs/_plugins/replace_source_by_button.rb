module Jekyll
    class ReplaceSourceByButton < Converter
        safe true
        priority :low

    def matches(ext)
        ext =~ /^\.md$/i
    end

    def output_ext(ext)
        ".html"
    end

    # HTML code comes from _includes/_doc-wrapper.html
    def convert(content)
        button_parts = content.match('div class="content-edit-page"')
        source_parts = content.match(/<a href="([^"]+)">\(source\)<\/a>/)
        if button_parts == nil and source_parts != nil
            url = source_parts[1]
            button_start = '<div class="content-edit-page"><a href="'
            button_end = '"><i class="fab fa-github"></i> <span>Edit Page</span></a></div>'
            button_start.concat(url.gsub('blob', 'edit'), button_end, content)
        else
            content
        end
    end
  end
end
