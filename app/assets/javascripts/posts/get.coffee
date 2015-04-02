$ ->
  id = $('#post-id').data('id')

  $.get "/posts/get/"+id, (repo) ->

    $('#post-container')

        .append ( $('<div>').attr 'class', 'post-title'
          .append( $('<h2>').text repo.post.title ) )

        .append ( $('<div>').attr 'class', 'post-body'
          .append( $('<p>').text repo.post.body ) )

        .append ( $('<div>').attr 'class', 'post-edit'
          .append( $('<a>')
            .attr 'href', '/posts/edit/' + repo.id
            .text 'Edit' ) )

