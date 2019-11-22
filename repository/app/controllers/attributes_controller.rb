class AttributesController < ApplicationController

    def create
        
        params.delete("controller")
        params.delete("action")
                
        @attribute = Attribute.new( { :attribute_name => params[:attributeName],
            :attribute_type => params[:type],
            :i_class_id => params[:classId] } )
        
        if @attribute.save
            render :json => @attribute, :status => :created
        else
            render :json => {}, :status => :unprocessable_entity
        end
    end

    def show
        render :json => Attribute.find(params[:id])
    end

    def index
        @attributes = Attribute.all 

        render :json => @attributes, :code => :ok
    end

end
