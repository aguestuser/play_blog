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

        .append ( $('<div>').attr 'class', 'post-edit'
          .append( $('<a>')
            .attr 'href', '/posts/edit/' + repo.id
            .text 'Edit' ) )

        .append ( $('<div>').attr 'class', 'list-posts'
          .append( $('<a>')
            .attr 'href', '/'
            .text 'All Posts' ) )



