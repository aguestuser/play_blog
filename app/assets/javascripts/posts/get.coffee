$ ->
  id = $('#post-id').data('id')

  $.get "/posts/get/"+id, (repo) ->

    console.log("repo", repo)

    if $.isEmptyObject(repo)

      $('#post-container')

        .append ( $('<div>').attr 'class', 'post-not-found'
          .append $('<p>').text "There is no post with id " + id)

    else

      $('#post-container')

        .append ( $('<div>').attr 'class', 'post-title'
          .append( $('<h2>').text repo.post.title ) )

        .append ( $('<div>').attr 'class', 'post-body'
          .append( $('<p>').text repo.post.body ) )

        .append( $('<div>')
          .attr 'class', 'post-meta'
          .text 'Created: ' + repo.created + ' | Last edited: ' + repo.modified )

        .append( $('<a>')
            .attr 'class', 'edit-post'
            .attr 'href', '/posts/edit/' + repo.id
            .text 'Edit' )

        .append($('<p>'))

        .append( $('<a>')
          .attr 'href', '/'
          .attr 'class', 'home'
          .text 'Home' )



