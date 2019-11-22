Rails.application.routes.draw do
  # For details on the DSL available within this file, see http://guides.rubyonrails.org/routing.html

  # Project routes
  
  resources :projects, except: :delete

  delete '/projects/:id', to: 'projects#destroy', as: 'project_destroy'

  delete '/projects', to: 'projects#clear', as: 'project_clear'

  # Package routes
  
  resources :packages, except: :delete

  delete '/packages/:id', to: 'packages#destroy', as: 'package_destroy'

  # Class routes
  
  resources :i_classes, except: :delete

  delete '/i_classes/:id', to: 'i_classes#destroy', as: 'class_destroy'

  # Attribute routes
  
  resources :attributes

  # Method routes
  
  resources :i_methods

  # Argument routes
  
  resources :arguments

  # Method Invocation routes
  
  resources :method_invocations

  # FullContents

  resources :full_contents

  # Events

  resources :events, except: :delete

  delete '/events', to: 'events#clear', as: 'event_clear'

  # Events arguments

  resources :event_arguments

  # Action cable websocket

  mount ActionCable.server => '/event_stream'
end
