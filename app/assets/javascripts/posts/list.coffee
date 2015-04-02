$ ->
  $.get "/posts/get/", (prs) ->
    $.each prs, (i,pr) ->
      $('#posts')
        .append ( ( $('<div>')
            .attr 'class', 'post-title' )
              .append( $('<h2>')
                .text pr.post.title ) )
        .append ( $('<div>')
            .attr 'class', 'post-body'
            .text pr.post.body )
        .append ( ( $('<div>')
            .attr 'class', 'post-edit')
              .append( $('<a>')
                .attr 'href', '/posts/edit/' + pr.id
                .text 'Edit' ) )

