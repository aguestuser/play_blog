$ ->
  $.get "/posts/get", (prs) ->
    $.each prs, (i,pr) ->
      $('#posts')
        .append ( ( $('<div>')
          .attr 'class', 'post-title' )
            .append( $('<h2>')
              .text pr.post.title ) )
        .append ( ( $('<div>')
          .attr 'class', 'post-created' )
            .append( $('<a>')
              .attr 'href', '/posts/'+pr.id
              .text pr.created ) )
        .append ( ($('<div>')
          .attr 'class', 'post-body')
            .append $('<p>').text pr.post.body )
        .append ( ( $('<div>')
            .attr 'class', 'post-edit')
              .append( $('<a>')
                .attr 'href', '/posts/edit/' + pr.id
                .attr 'class', 'edit-post'
                .text 'Edit' ) )

